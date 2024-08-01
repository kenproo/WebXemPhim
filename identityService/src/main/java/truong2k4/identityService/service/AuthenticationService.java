package truong2k4.identityService.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import truong2k4.identityService.exception.AppException;
import truong2k4.identityService.exception.ErrorCode;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.log4j.Log4j2;
import truong2k4.identityService.dtos.request.AuthenticationRequest;
import truong2k4.identityService.dtos.request.IntrospectRequest;
import truong2k4.identityService.dtos.request.LogoutRequest;
import truong2k4.identityService.dtos.request.RefreshRequest;
import truong2k4.identityService.dtos.response.AuthenticationResponse;
import truong2k4.identityService.dtos.response.IntrospectResponse;
import truong2k4.identityService.entity.InvalidatedToken;
import truong2k4.identityService.entity.User;
import truong2k4.identityService.repository.InvalidatedTokenRepository;
import truong2k4.identityService.repository.UserRepository;

@Log4j2
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
	UserRepository userRepository;
	InvalidatedTokenRepository invalidatedTokenRepository;
	@NonFinal
	@Value("${jwt.signerKey}")
	protected String SIGNER_KEY;

	@NonFinal
	@Value("${jwt.valid-duration}")
	protected long VALID_DURATION;

	@NonFinal
	@Value("${jwt.refreshable-duration")
	protected long REFRESHABLE_DURATION;

	// generate token
	public String generateToken(User user) {
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

		JWTClaimsSet jwtClaimSet = new JWTClaimsSet.Builder().subject(user.getUsername()).issuer("truong2k4.com")
				.issueTime(new Date())
				.expirationTime(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
				.jwtID((UUID.randomUUID().toString())).claim("scope", buildScope(user)).build();

		Payload payload = new Payload(jwtClaimSet.toJSONObject());
		JWSObject jwsObject = new JWSObject(header, payload);
		try {
			jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
			return jwsObject.serialize();
		} catch (JOSEException exception) {
			throw new RuntimeException();
		}
	}

	// build scope
	private String buildScope(User user) {
		StringJoiner stringJoiner = new StringJoiner(" ");

		if (!CollectionUtils.isEmpty(user.getRoles()))
			user.getRoles().forEach(role -> {
				stringJoiner.add("ROLE_" + role.getNameRole());
				if (!CollectionUtils.isEmpty(role.getPermissions()))
					role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
			});

		return stringJoiner.toString();
	}

	// introspect
	public IntrospectResponse introspect(IntrospectRequest introspectRequest) {
		String token = introspectRequest.getToken();
		boolean isValid = true;
		try {
			verifyToken(token, isValid);
		} catch (Exception e) {
			isValid = false;
		}
		return IntrospectResponse.builder().valid(isValid).build();

	}

	// verify token
	private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
		JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

		SignedJWT signedJWT = SignedJWT.parse(token);

		Date expiryTime = (isRefresh)
				? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant()
						.plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
				: signedJWT.getJWTClaimsSet().getExpirationTime();

		var verified = signedJWT.verify(verifier);

		if (!(verified && expiryTime.after(new Date())))
			throw new AppException(ErrorCode.UNAUTHENTICATED);

		if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
			throw new AppException(ErrorCode.UNAUTHENTICATED);

		return signedJWT;
	}

	// authenticate
	public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		User user = userRepository.findByUsername(authenticationRequest.getUsername())
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
		boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());
		if (!authenticated)
			throw new AppException(ErrorCode.UNAUTHENTICATED);
		var token = generateToken(user);
		return AuthenticationResponse.builder().token(token).build();
	}
  /// log out
	 public void logout(LogoutRequest request) throws ParseException, JOSEException {
	        try {
	            var signToken = verifyToken(request.getToken(), true);

	            String jit = signToken.getJWTClaimsSet().getJWTID();
	            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

	            InvalidatedToken invalidatedToken =
	                    InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

	            invalidatedTokenRepository.save(invalidatedToken);
	        } catch (AppException exception) {
	            log.info("Token already expired");
	        }
	    }

	    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
	        var signedJWT = verifyToken(request.getToken(), true);

	        var jit = signedJWT.getJWTClaimsSet().getJWTID();
	        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

	        InvalidatedToken invalidatedToken =
	                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

	        invalidatedTokenRepository.save(invalidatedToken);

	        var username = signedJWT.getJWTClaimsSet().getSubject();

	        var user =
	                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

	        var token = generateToken(user);

	        return AuthenticationResponse.builder().token(token).authenticated(true).build();
	    }
}
