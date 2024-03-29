﻿// <auto-generated />
using System;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Infrastructure;
using Microsoft.EntityFrameworkCore.Migrations;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;
using OnlineBank.LoanService.DAL;

#nullable disable

namespace OnlineBank.LoanService.DAL.Migrations
{
    [DbContext(typeof(LoanServiceDbContext))]
    [Migration("20240324065009_currency code")]
    partial class currencycode
    {
        /// <inheritdoc />
        protected override void BuildTargetModel(ModelBuilder modelBuilder)
        {
#pragma warning disable 612, 618
            modelBuilder
                .HasAnnotation("ProductVersion", "7.0.11")
                .HasAnnotation("Relational:MaxIdentifierLength", 63);

            NpgsqlModelBuilderExtensions.UseIdentityByDefaultColumns(modelBuilder);

            modelBuilder.Entity("OnlineBank.LoanService.DAL.Entities.LoanEntity", b =>
                {
                    b.Property<Guid>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("uuid");

                    b.Property<Guid>("BankAccountId")
                        .HasColumnType("uuid");

                    b.Property<string>("CurrencyCode")
                        .IsRequired()
                        .HasColumnType("text");

                    b.Property<decimal>("Debt")
                        .HasColumnType("numeric");

                    b.Property<DateTime>("EndDate")
                        .HasColumnType("timestamp with time zone");

                    b.Property<Guid>("LoanRateId")
                        .HasColumnType("uuid");

                    b.Property<decimal>("MonthlyPayment")
                        .HasColumnType("numeric");

                    b.Property<DateTime>("StartDate")
                        .HasColumnType("timestamp with time zone");

                    b.Property<Guid>("UserId")
                        .HasColumnType("uuid");

                    b.HasKey("Id");

                    b.HasIndex("LoanRateId");

                    b.ToTable("Loans");
                });

            modelBuilder.Entity("OnlineBank.LoanService.DAL.Entities.LoanRateEntity", b =>
                {
                    b.Property<Guid>("Id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("uuid");

                    b.Property<double>("InterestRate")
                        .HasColumnType("double precision");

                    b.Property<string>("Name")
                        .IsRequired()
                        .HasColumnType("text");

                    b.HasKey("Id");

                    b.ToTable("LoanRates");
                });

            modelBuilder.Entity("OnlineBank.LoanService.DAL.Entities.LoanEntity", b =>
                {
                    b.HasOne("OnlineBank.LoanService.DAL.Entities.LoanRateEntity", "LoanRate")
                        .WithMany()
                        .HasForeignKey("LoanRateId")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("LoanRate");
                });
#pragma warning restore 612, 618
        }
    }
}
