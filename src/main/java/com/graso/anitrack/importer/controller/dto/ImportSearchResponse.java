package com.graso.anitrack.importer.controller.dto;

import com.graso.anitrack.importer.model.Candidate;

import java.util.List;

public record ImportSearchResponse(List<Candidate> results) {
}
