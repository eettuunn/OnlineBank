using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace OnlineBank.LoanService.DAL.Migrations
{
    /// <inheritdoc />
    public partial class noloaninpayment : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_LoanPayments_Loans_LoanId",
                table: "LoanPayments");

            migrationBuilder.DropIndex(
                name: "IX_LoanPayments_LoanId",
                table: "LoanPayments");

            migrationBuilder.DropColumn(
                name: "LoanId",
                table: "LoanPayments");

            migrationBuilder.AddColumn<Guid>(
                name: "LoanEntityId",
                table: "LoanPayments",
                type: "uuid",
                nullable: true);

            migrationBuilder.CreateIndex(
                name: "IX_LoanPayments_LoanEntityId",
                table: "LoanPayments",
                column: "LoanEntityId");

            migrationBuilder.AddForeignKey(
                name: "FK_LoanPayments_Loans_LoanEntityId",
                table: "LoanPayments",
                column: "LoanEntityId",
                principalTable: "Loans",
                principalColumn: "Id");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_LoanPayments_Loans_LoanEntityId",
                table: "LoanPayments");

            migrationBuilder.DropIndex(
                name: "IX_LoanPayments_LoanEntityId",
                table: "LoanPayments");

            migrationBuilder.DropColumn(
                name: "LoanEntityId",
                table: "LoanPayments");

            migrationBuilder.AddColumn<Guid>(
                name: "LoanId",
                table: "LoanPayments",
                type: "uuid",
                nullable: false,
                defaultValue: new Guid("00000000-0000-0000-0000-000000000000"));

            migrationBuilder.CreateIndex(
                name: "IX_LoanPayments_LoanId",
                table: "LoanPayments",
                column: "LoanId");

            migrationBuilder.AddForeignKey(
                name: "FK_LoanPayments_Loans_LoanId",
                table: "LoanPayments",
                column: "LoanId",
                principalTable: "Loans",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
