package caimi.service.repository.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.apache.ignite.binary.BinaryObjectException;
import org.apache.ignite.binary.BinaryReader;
import org.apache.ignite.binary.BinaryWriter;
import org.apache.ignite.binary.Binarylizable;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MappedSuperclass
public abstract class AbstarctBussinessEntity extends AbstarctEntity implements Binarylizable{

	private static final Logger logger = LoggerFactory.getLogger(AbstarctBussinessEntity.class);
	
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
	
	@Column(name="entity_lables")
	@QuerySqlField(index=true)
	protected String lables;
	
	@Column(name = "entity_creation_time", columnDefinition="TIMESTAMP")
    @QuerySqlField
    protected LocalDateTime creationTime;
	
	@Column(name = "entity_last_activity_time", columnDefinition="TIMESTAMP")
    @QuerySqlField
    protected LocalDateTime lastActivityTime;

	@Override
	public void readBinary(BinaryReader arg0) throws BinaryObjectException {
		
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
        writer.writeObject("creationTime", creationTime);
        writer.writeObject("lastActivityTime", lastActivityTime);
	}

	@Override
	public String getIdAsString() {
		return null;
	}

	@Override
	public void setId(Serializable id) {
		
	}
	
	
}
