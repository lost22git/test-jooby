package lost.test.jooby;

import io.ebean.annotation.DbArray;
import io.ebean.annotation.Identity;
import io.ebean.annotation.Index;
import io.ebean.annotation.NotNull;
import io.soabase.recordbuilder.core.RecordBuilderFull;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.List;

@RecordBuilderFull
@Entity
@Table(name = "fighter")
@Index(
        unique = true,
        name = "uk_name",
        columnNames = {"name"})
public record Fighter(
        @Id @Identity Long id,
        @NotNull String name,
        @DbArray List<String> skill,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {}
