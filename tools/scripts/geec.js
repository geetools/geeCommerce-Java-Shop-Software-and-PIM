var imports = new JavaImporter(java.io, java.nio.file, java.lang);

var __cbHome = cbHome();
var __modulesDir = modulesDir();
var __projectsDir = projectsDir();

/*
	cb create module local/my-module
	cb remove module local/my-module

	cb add module core/catalog/catalog to project demo
	cb remove module core/catalog/catalog from project demo

	cb add all modules to project demo
	cb remove all modules from project demo
*/

load('http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.6.0/underscore-min.js')

var cmdFunctions = [
	{regex : 'create module (.+)', func : createSingleModule},
	{regex : 'remove module (.+)', func : removeSingleModule},
	{regex : 'add module (.+) to project (.+)', func : addSingleModuleToSingleProject},
	{regex : 'remove module (.+) from project (.+)', func : removeSingleModuleFromSingleProject},
	{regex : 'add all modules to project (.+)', func : addAllModulesToSingleProject},
	{regex : 'remove all modules from project (.+)', func : removeAllModulesFromSingleProject}
];

print('I am running from ' + __DIR__ + ' --> ' + __cbHome + ' - ' + __modulesDir + ' - ' + __projectsDir);

executeCommand(arguments);

function executeCommand(arguments) {
	var command = arguments.join(' ');

	cmdFunctions.forEach(function(cmdFunc) {
		var cmdMatches = command.match(cmdFunc.regex);
		
		if(cmdMatches) {
			if(cmdMatches.length == 2)
				cmdFunc.func(cmdMatches[1]);
			if(cmdMatches.length == 3)
				cmdFunc.func(cmdMatches[1], cmdMatches[2]);
			if(cmdMatches.length == 4)
				cmdFunc.func(cmdMatches[1], cmdMatches[2], cmdMatches[3]);
		}
	});
}

function createSingleModule(name) {
	print('create module-name:::: ' + name);
}

function removeSingleModule(name) {
	print('remove module-name:::: ' + name);
}

function addSingleModuleToSingleProject(moduleName, projectName) {
	print('addSingleModuleToSingleProject:::: ' + moduleName + ' ==> ' + projectName);
}

function removeSingleModuleFromSingleProject(moduleName, projectName) {
	print('removeSingleModuleFromSingleProject:::: ' + moduleName + ' ==> ' + projectName);
}

function addAllModulesToSingleProject(projectName) {
	
	var projDir = projectDir(projectName);
	var projModulesDir = projectModulesDir(projectName, true);
	
	var allModules = [];
	listAllModules(__modulesDir, allModules);

	allModules.forEach(function(modulePath) {
		var uniqueModuleName = uniqueName(modulePath);
		
		with (imports) {
			var moduleLinkFile = new File(projModulesDir, uniqueModuleName);
			
			System.out.println(moduleLinkFile.toPath() + ' ---- ' + new File(modulePath).toPath() + ' --- '  + Files.class);
			
			Files.createSymbolicLink(moduleLinkFile.toPath(), new File(modulePath).toPath());			
		}
		
	});
	
	
}

function removeAllModulesFromSingleProject(projectName) {
	print('removeAllModulesFromSingleProject:::: ' + projectName);
}

function cbHome() {
	with (imports) {
		var file = new File(__DIR__);
		return file.getParentFile().getParentFile().getAbsolutePath();
	}
}

function modulesDir() {
	with (imports) {
		var file = new File(__cbHome, 'modules');
		return file.getAbsolutePath();
	}
}

function projectsDir() {
	with (imports) {
		var file = new File(__cbHome, 'projects');
		return file.getAbsolutePath();
	}
}

function projectDir(name) {
	with (imports) {
		var projDir = new File(__projectsDir, name);
		
		if(projDir.exists() && projDir.isDirectory()) {
			return projDir.getAbsolutePath();
		}
	}
}

function projectModulesDir(name, createIfNotExist) {
	with (imports) {
		var projDir = projectDir(name);
		
		if(projDir) {
			var projModulesDir = new File(projDir, 'modules');
			
			if(!projModulesDir.exists() && createIfNotExist)
				projModulesDir.mkdir();
			
			return projModulesDir.getAbsolutePath();
		}
	}
}

function listAllModules(path, allModulesArr) {
	with (imports) {
		var dir = new File(path);
		
		if(!dir.exists() || !dir.isDirectory())
			return;
		
		var moduleProps = new File(dir.getAbsolutePath(), 'module.properties');
		
		if(moduleProps.exists()) {
			allModulesArr.push(moduleProps.getParentFile().getAbsolutePath());
		} else {
			var files = dir.listFiles();
			
			for(var i=0; i < files.length; i++) {
				var checkFile = files[i];
				
				if(checkFile.isDirectory()) {
					listAllModules(checkFile.getAbsolutePath(), allModulesArr);
				}
			}
		}
	}
}

function uniqueName(modulePath) {
	var newPath = modulePath.replace(/\\/g, '/');
	var moduesDirPos = newPath.indexOf('/modules/');
	
	return newPath.replace(/\//g, '-').substring(moduesDirPos + 9);
}
