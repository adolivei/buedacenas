package pt.ipleiria.estg.dei.ei.dae.daebackend.entities;

import jakarta.persistence.*;

@Entity
@NamedQueries({
        @NamedQuery(
                name = "getAllAdmins",
                query = "SELECT a FROM Admin a ORDER BY a.name"
        )
})
public class Admin extends User{
    public Admin() {
        super();
    }

    public Admin(String username, String password, String name, String email) {
        super(username, password, name, email);
    }
}
