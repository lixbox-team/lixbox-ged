desc_project{
    type="app-docker"
    withDocker=true
    withQuarkus=true
    version{
        majorVersion=11
        mediumVersion=1
        minorVersion=2
    }
    artefact{
        group="fr.lixbox.lixbox-ged"
        project="lixbox-ged"
        projectKey="${group}:${project}"
        dockerImageKey="lixboxteam"
    }
}

pic{
    channel="lixbox"
	git{
	    uri="https://github.com/lixbox-team/lixbox-ged.git"
	}    	
    jenkins{
        uri="https://ci.service.lixtec.fr/view/${channel}"
    }  
    sonar{
        uri="https://quality.service.lixtec.fr"
    }
    artifactory{
		uri="https://repos.service.lixtec.fr/artifactory"
    }
	mavencentral{
		uri="https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
	}
}

repository{
	artifactory{
	    release	="lixbox-release"
	    snapshot ="lixbox-snapshot"
	    libsRelease="libs-release"
	}
}