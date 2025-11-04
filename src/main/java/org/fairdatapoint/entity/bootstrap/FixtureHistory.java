package org.fairdatapoint.entity.bootstrap;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fairdatapoint.entity.base.BaseEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FixtureHistory extends BaseEntity {

    @NotNull
    @Column(unique = true)
    private String filename;

}
