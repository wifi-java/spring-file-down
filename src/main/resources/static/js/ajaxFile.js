const ajaxFile = (function () {
  const download = function (url) {
    const params = {
      url: url
    }

    $.ajax({
      url: '/download',
      data: params,
      type: 'get',
      xhrFields: {
        responseType: 'blob'
      },
      xhr: function () {
        const xhr = $.ajaxSettings.xhr();
        xhr.onprogress = function (progressEvent) {
          if (isNaN(progressEvent.total)) {
            console.log(`download: ${progressEvent.loaded}`)
          } else {
            let percent = Math.round((progressEvent.loaded * 100) / progressEvent.total)
            console.log(`download: ${percent}`)
          }
        }
        return xhr
      },
      success: function (data, statusText, xhr) {
        const blob = new Blob([data]);

        let fileName = xhr.getResponseHeader('content-disposition')
        if (fileName && fileName.indexOf('attachment') !== -1) {
          const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/
          const matches = filenameRegex.exec(fileName)
          if (matches != null && matches[1]) {
            fileName = matches[1].replace(/['"]/g, '')
          }
        }

        const a = document.createElement("a")
        a.href = window.URL.createObjectURL(blob)
        a.download = fileName
        a.click()

        console.log('download completed')
      },
      error: function (jqXHR, textStatus, errorThrown) {
        console.log(jqXHR);
      },
      complete: function () {
      }
    })
  }

  return {
    download: download,
  }
}())