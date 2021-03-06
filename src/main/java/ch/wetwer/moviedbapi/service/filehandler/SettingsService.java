package ch.wetwer.moviedbapi.service.filehandler;

import org.springframework.stereotype.Service;

/**
 * @author Wetwer
 * @project movie-score
 */
@Service
public class SettingsService {

    private PropertiesHandler propertiesHandler = new PropertiesHandler("settings.properties");

    public String getKey(String key) {
        return propertiesHandler.getProperty(key);
    }

    public void setValue(String key, String value) {
        propertiesHandler.setValue(key, value);
    }

}
