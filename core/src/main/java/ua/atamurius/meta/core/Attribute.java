package ua.atamurius.meta.core;

import static java.util.Objects.requireNonNull;

/**
 * Meta Type Attribute
 * Created by atamurius on 04.11.15.
 */
public class Attribute {

    private long id;
    private String name = "";
    private Type owner;
    private Domain domain; // TODO default domain

    Attribute(long id, Type owner) {
        this.id = id;
        this.owner = requireNonNull(owner);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = requireNonNull(name);
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = requireNonNull(domain);
    }

    public void dispose() {
        owner.deleteAttribute(this);
        owner = null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Attribute && this.id == ((Attribute) obj).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return (owner == null ? "deleted" : owner.getQualifiedName()) +"@"+ name;
    }
}
