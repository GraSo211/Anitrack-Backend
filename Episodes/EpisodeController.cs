using Microsoft.AspNetCore.Mvc;

[ApiController]
[Route("api/[controller]")]
public class EpisodeController : ControllerBase
{
    [HttpGet]
    public IActionResult GetEpisodes()
    {
        // Your logic to get episodes
        return Ok("okkkkkk");
    }

    [HttpPost]
    public IActionResult CreateEpisode([FromBody] EpisodeModel episode)
    {
        // Your logic to create an episode
        return CreatedAtAction(nameof(GetEpisodeById), new { id = episode.Id }, episode);
    }

    [HttpGet("{id}")]
    public IActionResult GetEpisodeById(int id)
    {
        // Your logic to get an episode by id
        return Ok();
    }

    [HttpPut("{id}")]
    public IActionResult UpdateEpisode(int id, [FromBody] EpisodeModel episode)
    {
        // Your logic to update an episode
        return NoContent();
    }

    [HttpDelete("{id}")]
    public IActionResult DeleteEpisode(int id)
    {
        // Your logic to delete an episode
        return NoContent();
    }
}