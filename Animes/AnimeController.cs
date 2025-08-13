

using Microsoft.AspNetCore.Mvc;

[ApiController]
[Route("api/[controller]")]
public class AnimeController : ControllerBase
{

    [HttpGet]
    public IActionResult GetAnimes()
    {
        return Ok(new List<string> { "Naruto", "One Piece", "Attack on Titan" });   
    }
}