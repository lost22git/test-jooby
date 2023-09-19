package lost.test.jooby;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;
import static lost.test.jooby.Result.ok;

import io.ebean.Database;
import io.ebean.annotation.Transactional;
import io.jooby.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lost.test.jooby.query.QFighter;

@Tag(name = "fighter", description = "fighter api")
@Path("/fighter")
public class FighterController {

    private final Database db;

    public FighterController(Database db) {
        this.db = db;
    }

    @Operation(summary = "查询所有 fighter")
    @GET
    public Result<List<Fighter>> findAll() {
        var all = db.find(Fighter.class).findList();
        return ok(all);
    }

    @Operation(summary = "查询一个 fighter, by name")
    @GET
    @Path("/{name}")
    public Result<Fighter> findByName(@PathParam String name) {
        var found = new QFighter().name.eq(name).findOneOrEmpty();
        return ok(found.orElse(null));
    }

    @Operation(summary = "新增一个 fighter")
    @Transactional
    @POST
    public Result<Fighter> add(FighterCreate fighterCreate) {
        var fighterInsert = FighterBuilder.builder()
                .name(fighterCreate.name())
                .addSkill(fighterCreate.skill())
                .createdAt(now(UTC))
                .build();

        db.save(fighterInsert);

        return ok(fighterInsert);
    }

    @Operation(summary = "编辑一个 fighter")
    @Transactional
    @PUT
    public Result<Fighter> edit(FighterEdit fighterEdit) {
        var found = new QFighter().name.eq(fighterEdit.name()).findOneOrEmpty().orElseThrow();
        var fighterUpdate = FighterBuilder.builder(found)
                .skill(fighterEdit.skill())
                .updatedAt(now(UTC))
                .build();
        db.update(fighterUpdate);
        return ok(fighterUpdate);
    }

    @Operation(summary = "删除一个 fighter")
    @Transactional
    @DELETE
    @Path("/{name}")
    public Result<Fighter> delete(@PathParam String name) {
        var found = new QFighter().name.eq(name).findOneOrEmpty();
        found.ifPresent(db::delete);
        return ok(found.orElse(null));
    }
}
