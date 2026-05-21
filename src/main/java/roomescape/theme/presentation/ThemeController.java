package roomescape.theme.presentation;

import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.theme.application.ThemeService;

@RestController
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping("/themes")
    public List<ThemeResponse> findAll() {
        return themeService.findAll().stream()
                .map(ThemeResponse::from)
                .toList();
    }

    @PostMapping("/themes")
    public ThemeResponse create(@RequestBody ThemeCreateRequest request) {
        return ThemeResponse.from(themeService.create(
                request.name(),
                request.description(),
                request.thumbnailUrl()
        ));
    }

    @DeleteMapping("/themes/{id}")
    public void delete(@PathVariable long id) {
        themeService.delete(id);
    }

    @GetMapping("/themes/popular")
    public List<PopularThemeResponse> findPopularThemes() {
        return themeService.findPopularThemes().stream()
                .map(PopularThemeResponse::from)
                .toList();
    }
}
