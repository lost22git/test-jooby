package lost.test.jooby;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.ebean.Database;
import io.jooby.Jooby;
import io.jooby.OpenAPIModule;
import io.jooby.ebean.EbeanModule;
import io.jooby.handler.CorsHandler;
import io.jooby.hikari.HikariModule;
import io.jooby.jackson.JacksonModule;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Jooby {

    {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        install(new JacksonModule(mapper));

        install(new HikariModule());
        install(new EbeanModule());

        var db = require(Database.class);
        initData(db);

        var openApiBasePath = getConfig().getConfig("openapi").getString("path");
        install(new OpenAPIModule(openApiBasePath).swaggerUI(openApiBasePath + "/ui"));

        setTrustProxy(true);

        use(new CorsHandler());

        mvc(new FighterController(db));
    }

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    static void initData(Database db) {
        LOG.info("初始化数据");
        try (var tx = db.beginTransaction()) {
            db.sqlUpdate("delete from fighter").execute();
            db.saveAll(List.of(
                    FighterBuilder.builder()
                            .name("隆")
                            .addSkill("波动拳")
                            .createdAt(now(UTC))
                            .build(),
                    FighterBuilder.builder()
                            .name("肯")
                            .addSkill("升龙拳")
                            .createdAt(now(UTC))
                            .build()));
            tx.commit();
        }
        LOG.info("初始化数据, 完成");
    }

    public static void main(String[] args) {
        runApp(args, Main::new);
    }
}
