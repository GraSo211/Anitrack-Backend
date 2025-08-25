using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Anitrack.Migrations
{
    /// <inheritdoc />
    public partial class AddingNewTable : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_EpisodeModel_User_UserModelId",
                table: "EpisodeModel");

            migrationBuilder.DropPrimaryKey(
                name: "PK_EpisodeModel",
                table: "EpisodeModel");

            migrationBuilder.RenameTable(
                name: "EpisodeModel",
                newName: "Episode");

            migrationBuilder.RenameIndex(
                name: "IX_EpisodeModel_UserModelId",
                table: "Episode",
                newName: "IX_Episode_UserModelId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Episode",
                table: "Episode",
                column: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_Episode_User_UserModelId",
                table: "Episode",
                column: "UserModelId",
                principalTable: "User",
                principalColumn: "Id");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Episode_User_UserModelId",
                table: "Episode");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Episode",
                table: "Episode");

            migrationBuilder.RenameTable(
                name: "Episode",
                newName: "EpisodeModel");

            migrationBuilder.RenameIndex(
                name: "IX_Episode_UserModelId",
                table: "EpisodeModel",
                newName: "IX_EpisodeModel_UserModelId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_EpisodeModel",
                table: "EpisodeModel",
                column: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_EpisodeModel_User_UserModelId",
                table: "EpisodeModel",
                column: "UserModelId",
                principalTable: "User",
                principalColumn: "Id");
        }
    }
}
