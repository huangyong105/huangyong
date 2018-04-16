package tech.huit.uuc.entity.auth;

import tech.huit.entity.AbstractEntity;
import java.util.Date; 
public class App extends AbstractEntity { 
	/** 业务系统名称 **/
	private String name;

	/** 授权SIP数量 **/
	private int authorizeSipCount;

	/** 授权并发通话数量 **/
	private int authorizeCallCount;

	/** 授权过期时间 **/
	private Date expiryDate;

	/** RSA公钥 **/
	private String rsaPubKey;

	/** RSA私钥 **/
	private String rsaPrivateKey;

	/** AES密码 **/
	private String aesKey;

	/** im回调地址 **/
	private String imCallbackUrl;

	public String getName(){
		return this.name;
	}
	public void setName(String name){
		this.name = name;
	}
	public int getAuthorizeSipCount(){
		return this.authorizeSipCount;
	}
	public void setAuthorizeSipCount(int authorizeSipCount){
		this.authorizeSipCount = authorizeSipCount;
	}
	public int getAuthorizeCallCount(){
		return this.authorizeCallCount;
	}
	public void setAuthorizeCallCount(int authorizeCallCount){
		this.authorizeCallCount = authorizeCallCount;
	}
	public Date getExpiryDate(){
		return this.expiryDate;
	}
	public void setExpiryDate(Date expiryDate){
		this.expiryDate = expiryDate;
	}
	public String getRsaPubKey(){
		return this.rsaPubKey;
	}
	public void setRsaPubKey(String rsaPubKey){
		this.rsaPubKey = rsaPubKey;
	}
	public String getRsaPrivateKey(){
		return this.rsaPrivateKey;
	}
	public void setRsaPrivateKey(String rsaPrivateKey){
		this.rsaPrivateKey = rsaPrivateKey;
	}
	public String getAesKey(){
		return this.aesKey;
	}
	public void setAesKey(String aesKey){
		this.aesKey = aesKey;
	}

	public String getImCallbackUrl() {
		return imCallbackUrl;
	}

	public void setImCallbackUrl(String imCallbackUrl) {
		this.imCallbackUrl = imCallbackUrl;
	}
}