package com.topway.reader.client.entity;

import java.util.Date;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("file_content")
public class FileContent {

	@Name
	@ColDefine(width=32)
	private String id;

	@Column
	@ColDefine(width=300)
	private String name;
	
	@Column
	private Long length;
	
	@Column
	@ColDefine(width=30)
	private String suffix;
	
	@Column
	private byte[] content;
	
	@Column
	private byte[] flashContent;
	
	@Column
	@ColDefine(width=50)
	private String md5;
	
	@Column
	@ColDefine(width=3000)
	private String srcFilePath;
	
	@Column
	@ColDefine(width=3000)
	private String flashFilePath;
	
	@Column
	@ColDefine(width=32)
	private String attachmentId;
	
	@Column
	@ColDefine(width=300)
	private String systemName;
	
	@Column
	private int convertCount;
	
	@Column
	@ColDefine(width=300)
	private String type;

	@Column
	@ColDefine(width=32)
	private String deptId;
	
	@Column
	@ColDefine(width=300)
	private String deptName;
	
	@Column
	@ColDefine(width=32)
	private String orgId; 

	@Column
	@ColDefine(width=300)
	private String orgName;
	
	@Column
	@ColDefine(width=32)
	private String creatorId;

	@Column
	@ColDefine(width=30)
	private String creatorName;
	
	@Column
	private Date createDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
	
	public byte[] getFlashContent() {
		return flashContent;
	}

	public void setFlashContent(byte[] flashContent) {
		this.content = flashContent;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getSrcFilePath() {
		return srcFilePath;
	}

	public void setSrcFilePath(String srcFilePath) {
		this.srcFilePath = srcFilePath;
	}

	public String getFlashFilePath() {
		return flashFilePath;
	}

	public void setFlashFilePath(String flashFilePath) {
		this.flashFilePath = flashFilePath;
	}

	public String getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public int getConvertCount() {
		return convertCount;
	}

	public void setConvertCount(int convertCount) {
		this.convertCount = convertCount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
}
