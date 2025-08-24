using Microsoft.AspNetCore.Mvc;

[ApiController]
[Route("api/[controller]")]
public class UserController : ControllerBase
{
    [HttpGet]
    public IActionResult GetUsers()
    {
        // Your logic to get users
        return Ok();
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