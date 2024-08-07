package pt.ipleiria.estg.dei.ei.dae.daebackend.entities;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;

@MappedSuperclass
public class Versionable {
	@Version
	private int version;
}