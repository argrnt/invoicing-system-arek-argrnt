package pl.futurecollars.invoicing.db.memory

import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.DatabaseTest

class InMemoryDatabaseTest extends DatabaseTest{

    @Override
    Database getDatabaseInstance() {
        return new InMemoryDatabase()
    }
}
