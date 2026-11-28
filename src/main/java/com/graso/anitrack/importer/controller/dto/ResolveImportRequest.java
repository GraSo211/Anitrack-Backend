package com.graso.anitrack.importer.controller.dto;

import com.graso.anitrack.importer.model.RowDecision;

import java.util.List;

public record ResolveImportRequest(List<RowDecision> decisions) {
}
