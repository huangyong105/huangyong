package tech.huit.uuc.entity.auth;

import tech.huit.entity.AbstractEntity;
import java.util.Date;
public class User extends AbstractEntity {
	/** 应用系统uid **/
	private String appUid;

	/** 用户昵称 **/
	private String nickname;

	/** 创建时间 **/
	private Date createTime;

	/** 最后登录时间 **/
	private Date lastLoginTime;

	/** 所属应用 **/
	private Integer appId;

	public String getAppUid(){
		return this.appUid;
	}
	public void setAppUid(String appUid){
		this.appUid = appUid;
	}
	public String getNickname(){
		return this.nickname;
	}
	public void setNickname(String nickname){
		this.nickname = nickname;
	}
	public Date getCreateTime(){
		return this.createTime;
	}
	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}
	public Date getLastLoginTime(){
		return this.lastLoginTime;
	}
	public void setLastLoginTime(Date lastLoginTime){
		this.lastLoginTime = lastLoginTime;
	}
	public Integer getAppId(){
		return this.appId;
	}
	public void setAppId(Integer appId){
		this.appId = appId;
	}
}