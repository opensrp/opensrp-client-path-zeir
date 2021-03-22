package org.smartregister.pathzeir.helper;

import org.smartregister.CoreLibrary;
import org.smartregister.domain.PhysicalLocation;
import org.smartregister.repository.LocationRepository;
import org.smartregister.sync.helper.ValidateAssignmentHelper;
import org.smartregister.util.SyncUtils;

import java.util.Set;
import java.util.stream.Collectors;

public class AppValidateAssignmentHelper extends ValidateAssignmentHelper {

    public AppValidateAssignmentHelper(SyncUtils syncUtils) {
        super(syncUtils);
    }

    /**
     * Validate Health Facility Level only
     *
     * @return
     */
    @Override
    protected Set<String> getExistingJurisdictions() {
        LocationRepository locationRepository = CoreLibrary.getInstance().context().getLocationRepository();
        return locationRepository.getAllLocations().stream()
                .filter(location -> location.getProperties().getGeographicLevel() == 4)
                .map(PhysicalLocation::getId).collect(Collectors.toSet());
    }

}