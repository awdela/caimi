package caimi.service.repository.entity;

import java.io.Serializable;

import javax.persistence.Transient;

import com.caimi.service.BORepository;
import com.caimi.service.repository.BOEntity;

public abstract class AbstarctEntity implements BOEntity{

	@Transient
	protected BORepository repository;
	
	public AbstarctEntity() {
		
	}
	
	public AbstarctEntity(BORepository repository) {
		this.repository = repository;
	}

	@Override
	@Transient
	public abstract String getIdAsString();

	@Override
	public BORepository getRepository() {
		return repository;
	}

	public void setRepository(BORepository repository) {
		this.repository = repository;
	}
	
	public abstract void setId(Serializable id);

	public void onRemove() {};
	
}
