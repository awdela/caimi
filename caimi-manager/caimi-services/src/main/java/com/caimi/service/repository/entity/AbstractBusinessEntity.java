package com.caimi.service.repository.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.ignite.binary.BinaryObjectException;
import org.apache.ignite.binary.BinaryReader;
import org.apache.ignite.binary.BinaryWriter;
import org.apache.ignite.binary.Binarylizable;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.json.JSONArray;
import org.json.JSONObject;

import com.caimi.service.repository.AbstractEntity;
import com.caimi.util.StringUtil;

@MappedSuperclass
public abstract class AbstractBusinessEntity extends AbstractEntity implements BusinessEntity, Binarylizable{

	@Id
	@Column(name="entity_id",nullable=false)
	@QuerySqlField(index=true)
	protected String id;
	
	@Column(name="entity_name")
	@QuerySqlField(index=true)
	protected String name;
	
	@Column(name="entity_desc")
	@QuerySqlField(index=true)
	protected String desc;
	
	@Column(name="entity_no")
	@QuerySqlField(index=true)
	protected String no;
	
	@Column(name="entity_status")
	@QuerySqlField(index=true)
	protected int status;
	
	@Column(name="entity_owner")
	@QuerySqlField(index=true)
	protected String ownerId;
	
	@Column(name="entity_parent")
	@QuerySqlField(index=true)
	protected String parentId;
	
	@Column(name="entity_labels")
	@QuerySqlField(index=true)
	protected String labels;
	
	@Column(name = "entity_creation_time", columnDefinition="TIMESTAMP")
    @QuerySqlField
    protected LocalDateTime creationTime;
	
	@Column(name = "entity_last_activity_time", columnDefinition="TIMESTAMP")
    @QuerySqlField
    protected LocalDateTime lastActivityTime;

	@Override
	public void readBinary(BinaryReader reader) throws BinaryObjectException {
		id = reader.readString("id");
        name = reader.readString("name");
        desc = reader.readString("desc");
        no = reader.readString("no");
        status = reader.readInt("status");
        ownerId = reader.readObject("ownerId");
        parentId = reader.readString("parentId");
        creationTime = reader.readObject("creationTime");
        lastActivityTime = reader.readObject("lastActivityTime");
        labels = reader.readString("labels");
	}

	@Override
	public void writeBinary(BinaryWriter writer) throws BinaryObjectException {
		writer.writeString("id", id);
        writer.writeString("name", name);
        writer.writeString("desc", desc);
        writer.writeString("no", no);
        writer.writeInt("status", status);
        writer.writeObject("ownerId", ownerId);
        writer.writeString("parentId", parentId);
        writer.writeString("labels", labels);
        writer.writeObject("creationTime", creationTime);
        writer.writeObject("lastActivityTime", lastActivityTime);
	}

	@Transient
	public String getIdAsString() {
		return id;
	}

	public void setId(Serializable id) {
		this.id = id.toString();
	}
	
	public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        json.put("id", getId())
        .put("name", getName())
        .put("no", getNo())
        .put("status", getStatus())
        .put("desc", getDesc())
        .put("ownerId", getOwnerId())
        .put("parentId", getParentId());

        if ( getCreationTime()!=null ) {
            json.put("creationTime", getCreationTime());
        }
        if ( getLastActivityTime()!=null ) {
            json.put("lastActivityTime", getLastActivityTime());
        }
        if ( !StringUtil.isEmpty(getLabels())) {
            json.put("labels", new JSONArray(getLabels().split(",")));
        }
        return json;
    }

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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}

	public LocalDateTime getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(LocalDateTime creationTime) {
		this.creationTime = creationTime;
	}

	public LocalDateTime getLastActivityTime() {
		return lastActivityTime;
	}

	public void setLastActivityTime(LocalDateTime lastActivityTime) {
		this.lastActivityTime = lastActivityTime;
	}
	
}
