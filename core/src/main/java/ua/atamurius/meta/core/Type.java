package ua.atamurius.meta.core;

import java.util.*;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * Meta Type.
 *
 * Created by atamurius on 04.11.15.
 */
public class Type {

    // stored
    private long id;
    private String group = "";
    private String name = "";
    private String description = "";
    private final Set<Type> baseTypes = new HashSet<>();
    private final List<Attribute> attributes = new ArrayList<>();
    // transient
    private final Set<Type> allBaseTypes = new HashSet<>();
    private final Set<Type> subtypes = new HashSet<>();
    private final List<Attribute> allAttributes = new ArrayList<>();
    private String qualifiedName = "";
    private Model model;
    // unmodifiable
    private final Set<Type> baseTypesU = Collections.unmodifiableSet(baseTypes);
    private final Set<Type> allBaseTypesU = Collections.unmodifiableSet(allBaseTypes);
    private final Set<Type> subtypesU = Collections.unmodifiableSet(subtypes);
    private final List<Attribute> attributesU = Collections.unmodifiableList(attributes);
    private final List<Attribute> allAttributesU = Collections.unmodifiableList(allAttributes);

    Type(Model model) {
        this.model = requireNonNull(model);
        this.id = model.nextId();
        updateQualifiedName(group, name);
    }

    public long getId() {
        return id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        updateQualifiedName(group, name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        updateQualifiedName(group, name);
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    private void updateQualifiedName(String group, String name) {
        String qname = requireNonNull(group) +":"+ requireNonNull(name);
        if (model.typesByName.containsKey(qname)) {
            throw new IllegalArgumentException("Type "+ qname +" already exists");
        }
        model.typesByName.remove(this.qualifiedName);
        this.qualifiedName = qname;
        this.group = group;
        this.name = name;
        model.typesByName.put(this.qualifiedName, this);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = requireNonNull(description);
    }

    public Set<Type> getBaseTypes() {
        return baseTypesU;
    }

    public Set<Type> getSubtypes() {
        return subtypesU;
    }

    public void forAnySubtype(Consumer<Type> f) {
        subtypes.forEach(f);
        subtypes.forEach(t -> t.forAnySubtype(f));
    }

    public void deriveFrom(Type type) {
        if (type.isDerivedFrom(this)) {
            throw new IllegalArgumentException("Circular derivation "+ type +" -> "+ this);
        }
        if (! this.isDerivedFrom(type)) {
            type.subtypes.add(this);
            baseTypes.add(type);
            allBaseTypes.add(type);
            allBaseTypes.addAll(type.allBaseTypes);
            recollectAttributes();
            forAnySubtype(sub -> {
                if (!sub.isDerivedFrom(type)) {
                    sub.allBaseTypes.add(type);
                    sub.allBaseTypes.addAll(type.allBaseTypes);
                    sub.recollectAttributes();
                }
            });
        }
    }

    private void recollectAttributes() {
        allAttributes.clear();
        allAttributes.addAll(attributes);
        allBaseTypes.forEach(t -> allAttributes.addAll(t.attributes));
    }

    public void underiveFrom(Type type) {
        if (this.isDerivedFrom(type)) {
            if (! baseTypes.contains(type)) {
                throw new IllegalArgumentException(this +" is not directly derived from "+ baseTypes);
            }
            baseTypes.remove(type);
            type.subtypes.remove(this);
            recollectBaseTypes();
            recollectAttributes();
            forAnySubtype(sub -> {
                sub.recollectBaseTypes();
                sub.recollectAttributes();
            });
        }
    }

    private void recollectBaseTypes() {
        allBaseTypes.clear();
        allBaseTypes.addAll(baseTypes);
        baseTypes.forEach(t -> allBaseTypes.addAll(t.allBaseTypes));
    }

    public boolean isDerivedFrom(Type type) {
        return allBaseTypes.contains(type);
    }

    public List<Attribute> getAttributes() {
        return attributesU;
    }

    public Attribute addAttribute() {
        Attribute attr = new Attribute(model.nextId(), this);
        model.attributes.put(attr.getId(), attr);
        attributes.add(attr);
        allAttributes.add(attr);
        forAnySubtype(sub -> sub.allAttributes.add(attr));
        return attr;
    }

    void deleteAttribute(Attribute attr) {
        attributes.remove(attr);
        model.attributes.remove(attr.getId());
        allAttributes.remove(attr);
        forAnySubtype(t -> t.allAttributes.remove(attr));
    }

    public Set<Type> getAllBaseTypes() {
        return allBaseTypesU;
    }

    public List<Attribute> getAllAttributes() {
        return allAttributesU;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Type && this.id == ((Type) obj).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return qualifiedName;
    }
}
