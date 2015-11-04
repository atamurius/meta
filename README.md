Meta model data management system
=================================

This project is intended to be universal
constructor of data management systems.
All objects are treated universally, types
are not code-dependant and can be changed
in run-time.

Base metamodel terms are **Type** and **Attribute**.
Type has attributes defined, classes derived from and
attributes derived from base types.

Each type can be derived from several other types,
getting all their attributes, and attributes their
are deriving from their base classes.