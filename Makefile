all:
	tsc ef-client/index.ts || echo "Compilation errors"
	cd RunPipeline; rm -rf node_modules; npm install; tsc || echo "Run pipeline compilation errors"
	cd PublishArtifact; rm -rf node_modules; npm install; tsc || echo "PublishArtifact compilation errors"
	perl publish.pl
