using AutoMapper;
using OnlineBank.UserService.Common.Dtos.User;
using OnlineBank.UserService.DAL.Entities;

namespace OnlineBank.UserService.BL;

public class UserServiceMapper : Profile
{
    public UserServiceMapper()
    {
        CreateMap<AppUser, UserDto>();
    }
}