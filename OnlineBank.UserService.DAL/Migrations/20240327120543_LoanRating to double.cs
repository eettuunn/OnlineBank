using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace OnlineBank.UserService.DAL.Migrations
{
    /// <inheritdoc />
    public partial class LoanRatingtodouble : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AlterColumn<double>(
                name: "LoanRating",
                table: "AspNetUsers",
                type: "double precision",
                nullable: false,
                oldClrType: typeof(int),
                oldType: "integer");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AlterColumn<int>(
                name: "LoanRating",
                table: "AspNetUsers",
                type: "integer",
                nullable: false,
                oldClrType: typeof(double),
                oldType: "double precision");
        }
    }
}
