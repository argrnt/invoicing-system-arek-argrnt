package pl.futurecollars.invoicing.db.memory

import pl.futurecollars.invoicing.db.Database

class InMemoryDatabaseTest extends DatabaseTest{

    @Override
    Database getDatabaseInstance() {
        return new InMemoryDatabase()
    }
}
