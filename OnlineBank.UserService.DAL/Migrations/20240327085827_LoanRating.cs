using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace OnlineBank.UserService.DAL.Migrations
{
    /// <inheritdoc />
    public partial class LoanRating : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "LoanRating",
                table: "AspNetUsers",
                type: "integer",
                nullable: false,
                defaultValue: 0);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "LoanRating",
                table: "AspNetUsers");
        }
    }
}
