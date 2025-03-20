package org.jhipster.space.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A Mission.
 */
@Entity
@Table(name = "mission")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Mission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @JsonIgnoreProperties(value = { "mission" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "mission")
    private SpaceEvent spaceEvent;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Mission id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Mission name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Mission description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SpaceEvent getSpaceEvent() {
        return this.spaceEvent;
    }

    public void setSpaceEvent(SpaceEvent spaceEvent) {
        if (this.spaceEvent != null) {
            this.spaceEvent.setMission(null);
        }
        if (spaceEvent != null) {
            spaceEvent.setMission(this);
        }
        this.spaceEvent = spaceEvent;
    }

    public Mission spaceEvent(SpaceEvent spaceEvent) {
        this.setSpaceEvent(spaceEvent);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Mission)) {
            return false;
        }
        return getId() != null && getId().equals(((Mission) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Mission{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
