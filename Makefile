NAME=snowowl
RELEASE_VERSION=$(shell git rev-parse HEAD)
SEMVER_VERSION=$(shell git describe --abbrev=0 --tags)
REPO=quay.io/babylonhealth
DEPLOY_DEV_URL=http://dev-ai-deploy.babylontech.co.uk:5199/job/kube-deploy-dev/buildWithParameters
DEPLOY_STAGING_URL=http://dev-ai-deploy.babylontech.co.uk:5199/job/kube-deploy-staging/buildWithParameters
SNOWOWL_INSTALL_PACKAGE=$(shell find ./releng/com.b2international.snowowl.server.update/target -name "snow-owl-oss*.tar.gz")


build-project:
	./mvnw clean verify -DskipTests

build-docker:
	cp "${SNOWOWL_INSTALL_PACKAGE}" "./docker/${`basename "${SNOWOWL_INSTALL_PACKAGE}"`}"
	docker build ./docker \
	--build-arg SNOWOWL_INSTALL_PACKAGE=`basename "${SNOWOWL_INSTALL_PACKAGE}"` \
	--build-arg BUILD_TIMESTAMP=`date +%s` \
	--build-arg TAG="${RELEASE_VERSION}" \
	--build-arg GIT_REVISION="${RELEASE_VERSION}" \
	-t $(REPO)/$(NAME):$(RELEASE_VERSION)

push:
	docker push $(REPO)/$(NAME):$(RELEASE_VERSION)

pull:
	docker pull $(REPO)/$(NAME):$(RELEASE_VERSION)

run:
	docker run --rm --name $(NAME) \
    --cpus="3" \
    --cpuset-cpus="3" \
    -d \
    -e ELASTICSEARCH_CLUSTER_URL="${ELASTICSEARCH_CLUSTER_URL}" \
    -e ELASTICSEARCH_CONNECT_TIMEOUT="${ELASTICSEARCH_CONNECT_TIMEOUT}" \
    -e ELASTICSEARCH_SOCKET_TIMEOUT="${ELASTICSEARCH_SOCKET_TIMEOUT}" \
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

deploy-dev:
	@curl -vvv -XPOST "${DEPLOY_DEV_URL}?token=${JENKINS_DEV_TOKEN}&APP=snowowl&VERSION=${RELEASE_VERSION}"

deploy-staging:
	docker login -u "${DOCKER_USER}" -p "${DOCKER_PASS}" quay.io
	make pull
	make tag-semver
	@curl -vvv -XPOST "${DEPLOY_STAGING_URL}?token=${JENKINS_STAGING_TOKEN}&APP=snowowl&VERSION=${SEMVER_VERSION}"