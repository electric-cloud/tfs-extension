all:
	tsc ef-client/index.ts
	cd RunPipeline; npm install ../ef-client; tsc
	cd PublishArtifact && npm install ../ef-client && tsc
	perl publish.pl
