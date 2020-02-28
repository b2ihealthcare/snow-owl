NAME=chr-terminology-server
GIT_VERSION=$(shell git rev-parse HEAD)
SEMVER_VERSION=$(shell git describe --abbrev=0 --tags)
REPO=quay.io/babylonhealth
DEPLOY_DEV_URL=http://dev-ai-deploy.babylontech.co.uk:5199/job/kube-deploy-dev/buildWithParameters
DEPLOY_STAGING_URL=http://dev-ai-deploy.babylontech.co.uk:5199/job/kube-deploy-staging/buildWithParameters
SNOWOWL_RPM_PACKAGE=$(shell find ./releng/com.b2international.snowowl.server.update/target -name "snow-owl-oss*.rpm")


# Please see README for memory configuration
build:
	mvn clean verify -Dmaven.test.skip=true
	cp $(SNOWOWL_RPM_PACKAGE)  ./docker/`basename "${SNOWOWL_RPM_PACKAGE}"`
	docker build ./docker \
	--build-arg SNOWOWL_RPM_PACKAGE=`basename "${SNOWOWL_RPM_PACKAGE}"` \
	--build-arg BUILD_TIMESTAMP=`date +%s` \
	--build-arg VERSION="${RELEASE_VERSION}" \
	--build-arg GIT_REVISION="${GIT_VERSION}" \
	-t $(REPO)/$(NAME):$(RELEASE_VERSION)

run:
	docker run --rm --name snowowlTest \
    --cpus="4" \
    -v /sys/fs/cgroup:/sys/fs/cgroup:ro \
    -d \
    -e ELASTICSEARCH_CLUSTER_URL="${ELASTICSEARCH_CLUSTER_URL}" \
    -e ELASTICSEARCH_CONNECT_TIMEOUT="${ELASTICSEARCH_CONNECT_TIMEOUT}" \
    -e ELASTICSEARCH_SOCKET_TIMEOUT="${ELASTICSEARCH_SOCKET_TIMEOUT}" \
    -p 28082:8080 $(REPO)/$(NAME):$(RELEASE_VERSION)

tag-develop:
	docker tag  $(REPO)/$(NAME):$(RELEASE_VERSION) $(REPO)/$(NAME):develop
	docker push $(REPO)/$(NAME):develop

tag-semver:
	@if docker run --rm -e DOCKER_REPO=babylonhealth/$(NAME) -e DOCKER_TAG=$(SEMVER_VERSION) quay.io/babylonhealth/tag-exists; then \
		echo "Tag $(SEMVER_VERSION) already exists!" && exit 1; \
	else \
		docker tag $(REPO)/$(NAME):$(RELEASE_VERSION) $(REPO)/$(NAME):$(SEMVER_VERSION); \
		docker push $(REPO)/$(NAME):$(SEMVER_VERSION); \
		docker tag $(REPO)/$(NAME):$(SEMVER_VERSION) $(REPO)/$(NAME):master; \
		docker push $(REPO)/$(NAME):master; \
	fi

pull:
	docker pull $(REPO)/$(NAME):$(RELEASE_VERSION)

install: build
	docker push $(REPO)/$(NAME):$(RELEASE_VERSION)

deploy-dev:
	@curl -vvv -XPOST "${DEPLOY_DEV_URL}?token=${JENKINS_DEV_TOKEN}&APP=chr-terminology-server&VERSION=${RELEASE_VERSION}"

deploy-staging:
	docker --version
	docker login -u "${DOCKER_USER}" -p "${DOCKER_PASS}" quay.io
	make pull
	make tag-semver
	@curl -vvv -XPOST "${DEPLOY_STAGING_URL}?token=${JENKINS_STAGING_TOKEN}&APP=chr-terminology-server&VERSION=${SEMVER_VERSION}"
