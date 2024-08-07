package pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions;

public class MyEntityExistsException extends Exception{
    public MyEntityExistsException(String message) {
        super(message);
    }
}