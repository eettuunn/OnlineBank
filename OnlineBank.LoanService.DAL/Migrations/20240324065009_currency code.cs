using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace OnlineBank.LoanService.DAL.Migrations
{
    /// <inheritdoc />
    public partial class currencycode : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "CurrencyCode",
                table: "Loans",
                type: "text",
                nullable: false,
                defaultValue: "");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "CurrencyCode",
                table: "Loans");
        }
    }
}
