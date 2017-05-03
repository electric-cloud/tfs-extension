all:
	tsc ef-client/index.ts || echo "Compilation errors"
	cd RunPipeline; npm install; tsc || echo "Run pipeline compilation errors"
	cd PublishArtifact; npm install; tsc || echo "PublishArtifact compilation errors"
	perl publish.pl
