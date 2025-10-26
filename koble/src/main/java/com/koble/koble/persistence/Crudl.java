package com.koble.koble.persistence;

import java.util.List;

//Letter T is a generic type that will be replaced by a specific class when the interface is implemented.
//This allows the interface to be used with any type of entity.

public interface Crudl<T> {

    // Method to create a new register in the Database;
    public T create(T entity);

    // Method to delete a register from the Database by its id;
    public String delete(long idEntity);

    // Method to update an existing register in the Database;
    public T update(long idEntity, T entity);

    // Method to read a register from the Database by its id;
    public T read(long idEntity);

    // Method to read all registers from the Database by its id;
    public List<T> listAll();
    
}

