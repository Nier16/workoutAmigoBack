package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.acme.converter.ListStringConverter;
import org.acme.model.ExerciseLevel;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.persistence.*;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Entity
public class Exercise extends PanacheEntity {

    @FormParam("name")
    @PartType(MediaType.TEXT_PLAIN)
    public String name;

    @FormParam("description")
    @PartType(MediaType.TEXT_PLAIN)
    public String description;

    @FormParam("muscles")
    @PartType(MediaType.TEXT_PLAIN)
    @Convert(converter = ListStringConverter.class)
    public List<String> muscles;

    @FormParam("img")
    @PartType(MediaType.TEXT_PLAIN)
    public String img;

    @FormParam("level")
    @PartType(MediaType.TEXT_PLAIN)
    public ExerciseLevel level;
}
