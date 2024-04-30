using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace OnlineBank.MonitoringService.Migrations
{
    /// <inheritdoc />
    public partial class Initial : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Requests",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uuid", nullable: false),
                    Url = table.Column<string>(type: "text", nullable: false),
                    Method = table.Column<string>(type: "text", nullable: false),
                    Protocol = table.Column<string>(type: "text", nullable: false),
                    Status = table.Column<int>(type: "integer", nullable: false),
                    SpentTimeInMs = table.Column<double>(type: "double precision", nullable: false),
                    ApiName = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Requests", x => x.Id);
                });
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "Requests");
        }
    }
}
