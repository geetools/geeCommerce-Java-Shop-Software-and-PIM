package com.geecommerce.core.security;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;

import com.geecommerce.core.authentication.Passwords;
import com.geecommerce.core.config.MerchantConfig;

public class DefaultCredentialsMatcher implements CredentialsMatcher {
    public static final String DEFAULT_WEB_USERNAME = "web-user";

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
	if (token instanceof UsernamePasswordToken) {
	    UsernamePasswordToken upToken = (UsernamePasswordToken) token;

	    if (DEFAULT_WEB_USERNAME.equals(upToken.getUsername())) {
		return true;
	    }

	    // Password sent via HTTP.
	    char[] attemptPassword = upToken.getPassword();
	    // Password from user collection in DB.
	    byte[] userPassword = getCredentials(info);
	    // Salt from user collection in DB.
	    byte[] userSalt = getSalt(info);

	    try {
		if (attemptPassword == null || userPassword == null || userSalt == null || attemptPassword.length == 0 || userPassword.length == 0 || userSalt.length == 0) {
		    return false;
		}

		return Passwords.authenticate(String.valueOf(attemptPassword), userPassword, getSalt(userSalt));
	    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
		throw new RuntimeException(e.getMessage(), e);
	    }
	} else {
	    throw new RuntimeException("The AuthenticationToken must be of type 'org.apache.shiro.authc.UsernamePasswordToken'");
	}
    }

    private byte[] getCredentials(AuthenticationInfo info) {
	return (byte[]) info.getCredentials();
    }

    private byte[] getSalt(AuthenticationInfo info) {
	return ((SaltedAuthenticationInfo) info).getCredentialsSalt().getBytes();
    }

    public static byte[] encryptPassword(String password, byte[] randomSalt) throws NoSuchAlgorithmException, InvalidKeySpecException {
	if (password == null || randomSalt == null || randomSalt.length == 0)
	    throw new NullPointerException("Password and/or random salt cannot be null");

	return Passwords.getEncryptedPassword(password, getSalt(randomSalt));
    }

    private static byte[] getSalt(byte[] randomSalt) throws NoSuchAlgorithmException {
	if (randomSalt == null || randomSalt.length == 0)
	    throw new NullPointerException("Random salt cannot be null");

	byte[] sugar = MerchantConfig.GET.val(MerchantConfig.BACKEND_SECURITY_SUGAR).getBytes();

	if (sugar == null || sugar.length == 0)
	    throw new NullPointerException("Merchant sugar cannot be null");

	return Passwords.merge(randomSalt, sugar);
    }
}
