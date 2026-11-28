package com.graso.anitrack.importer.controller.dto;

import java.util.List;

public record StartImportRequest(List<Integer> overwriteMalIds, List<Integer> excludedMalIds) {
}
