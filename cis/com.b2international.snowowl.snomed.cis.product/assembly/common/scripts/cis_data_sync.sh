#!/usr/bin/env bash
#
# Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#
# Snow Owl terminology server CIS synchronization script
# Parameters that must be configured before execution
#

# Set of source Snow Owl Terminology Servers to access for CIS data
SNOW_OWL_HOSTS=("<snowowl_host1>" "<snowowl_host2>")

# Change this to the Elasticsearch port used by Snow Owl under the hood, default value is 9200
ES_PORT=9200

execute() {

  for i in "${SNOW_OWL_HOSTS[@]}"; do
    DATA=$(cat <<EOF
{
  "source": {
    "remote": {
      "host": "http://${i}:${ES_PORT}"
    },
    "index": "snomedids-sctid",
    "type": "sctid",
    "query": {
      "match_all": {}
    }
  },
  "dest": {
    "index": "snomedids-sctid",
    "type": "sctid"
  }
}
EOF
)
    echo "Synchronizing ${i}..."
    curl -XPOST -H "Content-Type: application/json" \
      --silent --show-error \
      http://localhost:${ES_PORT}/_reindex?wait_for_completion=true -d "${DATA}"

  done


  exit 0
}

execute
