using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace OnlineBank.UserService.DAL.Migrations
{
    /// <inheritdoc />
    public partial class Baninuser : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<bool>(
                name: "Ban",
                table: "AspNetUsers",
                type: "boolean",
                nullable: false,
                defaultValue: false);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "Ban",
                table: "AspNetUsers");
        }
    }
}
