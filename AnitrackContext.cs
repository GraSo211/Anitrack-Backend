using Microsoft.EntityFrameworkCore;

public class AnitrackContext : DbContext
{
    public AnitrackContext(DbContextOptions<AnitrackContext> options)
        : base(options)
    {
    }

    public DbSet<UserModel> Users { get; set; }
    public DbSet<EpisodeModel> Episodes { get; set; }
    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);
        modelBuilder.Entity<UserModel>().ToTable("User");
        modelBuilder.Entity<UserModel>().HasIndex(u => u.Name).IsUnique();



        modelBuilder.Entity<EpisodeModel>().ToTable("Episode");

    }


}
