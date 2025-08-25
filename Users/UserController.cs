using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

[ApiController]
[Route("api/[controller]")]
public class UserController : ControllerBase
{

    private AnitrackContext _dbcontext;

    public UserController(AnitrackContext dbcontext)
    {
        _dbcontext = dbcontext;
    }

    [HttpGet]
    public IActionResult GetUsers()
    {
        // Your logic to get users
        var users = _dbcontext.Users.ToList();
        return Ok(users);
    }

    [HttpPost]
    public IActionResult CreateUser([FromBody] UserModel user)
    {
        // Your logic to create a user
        return CreatedAtAction(nameof(GetUserById), new { id = user.Id }, user);
    }

    [HttpGet("{id}")]
    public IActionResult GetUserById(int id)
    {
        // Your logic to get a user by id
        return Ok();
    }

    [HttpPut("{id}")]
    public IActionResult UpdateUser(int id, [FromBody] UserModel user)
    {
        // Your logic to update a user
        return NoContent();
    }

    [HttpDelete("{id}")]
    public IActionResult DeleteUser(int id)
    {
        // Your logic to delete a user
        return NoContent();
    }
}