package pl.futurecollars.invoicing.db.mongo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.IfProfileValue
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.DatabaseTest

@SpringBootTest
@IfProfileValue(name = "spring.profiles.active", value = "mongo")
class MongoDatabaseIntegrationTest extends DatabaseTest {

    @Autowired
    private MongoBasedDatabase mongoDatabase

    @Override
    Database getDatabaseInstance() {
        assert mongoDatabase != null
        mongoDatabase
    }

}
