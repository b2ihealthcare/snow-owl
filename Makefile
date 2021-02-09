NAME=snowowl
RELEASE_VERSION=$(shell git rev-parse HEAD)
SEMVER_VERSION=$(shell git describe --abbrev=0 --tags)
REPO=quay.io/babylonhealth

build-docker:
	docker build ./custom_docker \
	-t $(REPO)/$(NAME):$(RELEASE_VERSION)

push:
	docker push $(REPO)/$(NAME):$(RELEASE_VERSION)

pull:
	docker pull $(REPO)/$(NAME):$(RELEASE_VERSION)

run:
	docker run --rm --name $(NAME) \
    -p 8080:8080 $(REPO)/$(NAME):$(RELEASE_VERSION)

tag-semver:
	@if docker run --rm -e DOCKER_REPO=babylonhealth/$(NAME) -e DOCKER_TAG=$(SEMVER_VERSION) quay.io/babylonhealth/tag-exists; then \
		echo "Tag $(SEMVER_VERSION) already exists!" && exit 1; \
	else \
		docker tag $(REPO)/$(NAME):$(RELEASE_VERSION) $(REPO)/$(NAME):$(SEMVER_VERSION); \
		docker push $(REPO)/$(NAME):$(SEMVER_VERSION); \
		docker tag $(REPO)/$(NAME):$(SEMVER_VERSION) $(REPO)/$(NAME):master; \
		docker push $(REPO)/$(NAME):master; \
	fi

publish-staging:
	docker login -u "${DOCKER_USER}" -p "${DOCKER_PASS}" quay.io
	make pull
	make tag-semver