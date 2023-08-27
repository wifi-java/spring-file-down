const axiosFile = (function () {
  const download = function (url) {
    const params = {
      url: url
    }

    const option = {
      method: 'GET'
      , params
      , responseType: 'blob'
      , onDownloadProgress: (progressEvent) => {
        if (isNaN(progressEvent.total)) {
          console.log(`download: ${progressEvent.loaded}`)
        } else {
          let percent = Math.round((progressEvent.loaded * 100) / progressEvent.total)
          console.log(`download: ${percent}`)
        }
      },
    }

    axios.get('/download', option)
      .then((response) => {
        const blob = new Blob([response.data])
        let fileName = response.headers['content-disposition']

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
      })
      .catch((error) => {
        console.log(error)
      }).finally({})
  }

  return {
    download: download,
  }
}())