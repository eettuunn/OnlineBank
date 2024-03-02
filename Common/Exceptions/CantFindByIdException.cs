namespace Common.Exceptions;

public class CantFindByIdException : Exception
{
    public CantFindByIdException(string what, Guid id) : base($"Can't find {what} with Id {id}")
    {
    }

    public CantFindByIdException(string what, Guid? id): base($"Can't find {what} with Id {id}")
    {
    }
}